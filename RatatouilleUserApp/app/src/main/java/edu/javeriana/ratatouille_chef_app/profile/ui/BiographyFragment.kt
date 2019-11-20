package edu.javeriana.ratatouille_chef_app.profile.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import edu.javeriana.ratatouille_chef_app.R
import edu.javeriana.ratatouille_chef_app.authentication.entities.Biography
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Recipe
import edu.javeriana.ratatouille_chef_app.core.toObjectWithId
import edu.javeriana.ratatouille_chef_app.profile.viewmodels.BiographyViewModel
import kotlinx.android.synthetic.main.fragment_biography.*
import org.jetbrains.anko.onClick

class BiographyFragment : Fragment() {

    private var biographyViewModel: BiographyViewModel? = null
    private val args: BiographyFragmentArgs by navArgs()

    private val biographyObserver = Observer<Biography> { biography ->
        formationText.text = biography.formation
        yearsOfExperienceText.text = biography.yearsOfExperience.toString()
        populateChips(certificatesChipGroup, biography.certificates)
        populateChips(specialitiesChipGroup, biography.specialities)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_biography, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        fetchViewModels()
        setUpLiveDataListeners()
        biographyViewModel?.findChefBiographyById(args.chefId!!)
        setupRecipes()
    }


    private fun setupRecipes() {
        biographyViewModel?.findUserReference(args.chefId!!)?.addOnCompleteListener { it ->
            val user = it.result?.toObject(User::class.java)
            user?.recipes?.forEach {
                it.get().addOnSuccessListener { recipe ->
                    val chipItem = Chip(recepieChipGroup.context)
                    val re = recipe.toObjectWithId<Recipe>()
                    chipItem.text = recipe.toObject(Recipe::class.java)?.name
                    chipItem.onClick {
                        val action =
                            BiographyFragmentDirections.actionBiographyFragmentToNewRequestDetail(
                                re.id,
                                args.address
                            )
                        view?.findNavController()?.navigate(action)
                    }
                    recepieChipGroup.addView(chipItem)
                }
            }
        }
    }

    private fun fetchViewModels() {
        biographyViewModel = ViewModelProviders.of(this).get(BiographyViewModel::class.java)
    }

    private fun setUpLiveDataListeners() {
        biographyViewModel?.biographyLiveData?.observe(this, biographyObserver)
    }

    private fun populateChips(chipGroup: ChipGroup, items: List<String>) {
        items.forEach {
            val chipItem = Chip(chipGroup.context)
            chipItem.text = it
            chipGroup.addView(chipItem)
        }
    }


}
